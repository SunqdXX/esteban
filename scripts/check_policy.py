import pathlib
import re
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
RULES = {
    ".java": re.compile(r"^\s*(//|/\*|\*)"),
    ".gradle": re.compile(r"^\s*(//|/\*|\*)"),
    ".py": re.compile(r"^\s*#"),
    ".yml": re.compile(r"^\s*#"),
    ".properties": re.compile(r"^\s*[#!]"),
    ".bat": re.compile(r"^\s*(@?rem\b|::)", re.IGNORECASE),
}
SHELL = re.compile(r"^\s*#(?!!)")


def tracked():
    listed = subprocess.run(
        ["git", "ls-files", "-z", "--cached", "--others", "--exclude-standard"],
        cwd=ROOT,
        capture_output=True,
        check=True,
    ).stdout.decode().split("\0")
    return [ROOT / name for name in listed if name and (ROOT / name).is_file()]


def main():
    problems = []
    for path in tracked():
        rule = SHELL if path.name == "gradlew" else RULES.get(path.suffix)
        if rule is None:
            continue
        text = path.read_text(encoding="utf-8", errors="ignore")
        for number, line in enumerate(text.splitlines(), 1):
            if rule.search(line):
                problems.append(f"{path.relative_to(ROOT)}:{number}: comment line")
    for problem in problems:
        print(problem)
    if problems:
        sys.exit(1)
    print("policy ok")


main()
