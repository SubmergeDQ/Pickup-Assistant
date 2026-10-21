# -*- coding: utf-8 -*-
"""从 Destination.java 生成 Web 入口与 PWA 快捷方式，保持深链地址单一来源。

修改 Destination.java 后运行：py build_scripts/sync_web.py
"""
import html
import json
import os
import re

ROOT = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
DESTINATION = os.path.join(
    ROOT, "app", "src", "main", "java", "com", "pickup", "tool", "Destination.java"
)
INDEX = os.path.join(ROOT, "web", "index.html")
MANIFEST = os.path.join(ROOT, "web", "manifest.webmanifest")

# Web 版只暴露三个最高频入口，顺序与页面一致。
WEB_KEYS = ("CAINIAO", "TAOBAO", "PINDUODUO")


def read_text(path):
    with open(path, encoding="utf-8") as file:
        return file.read()


def write_text(path, content):
    with open(path, "w", encoding="utf-8", newline="\n") as file:
        file.write(content)


def parse_destinations(source):
    """解析枚举常量：名称 → key / title / label / subtitle / mark / appUris / webUri。"""
    matches = list(re.finditer(r"^    ([A-Z][A-Z0-9_]*)\($", source, re.M))
    destinations = {}
    for index, match in enumerate(matches):
        end = (
            matches[index + 1].start()
            if index + 1 < len(matches)
            else source.index(";", match.end())
        )
        block = source[match.end():end]
        uris_match = re.search(r"new String\[\]\s*\{(.*?)\}", block, re.S)
        if uris_match is None:
            raise ValueError("无法解析 " + match.group(1) + " 的 appUris")
        strings = re.findall(r'"((?:[^"\\]|\\.)*)"', block)
        if len(strings) < 6:
            raise ValueError("无法解析 " + match.group(1) + " 的字段")
        destinations[match.group(1)] = {
            "key": strings[0],
            "title": strings[1],
            "label": strings[2],
            "subtitle": strings[3],
            "mark": strings[4],
            "app_uris": re.findall(r'"((?:[^"\\]|\\.)*)"', uris_match.group(1)),
            "web_uri": strings[-1],
        }
    return destinations


def short_name(destination):
    # “菜鸟驿站取件”“淘宝取件”“拼多多取件” → “菜鸟”“淘宝”“拼多多”
    return destination["label"].replace("驿站", "").replace("取件", "")


def render_entry(destination):
    uri = html.escape(destination["app_uris"][0])
    return (
        f'      <a class="entry {destination["key"]}" href="{uri}">\n'
        f'        <span class="mark" aria-hidden="true">{html.escape(destination["mark"])}</span>\n'
        f'        <span class="label">\n'
        f'          <strong>{html.escape(destination["label"])}</strong>\n'
        f'          <span>{html.escape(destination["subtitle"])}</span>\n'
        f'        </span>\n'
        f'        <span class="arrow" aria-hidden="true">›</span>\n'
        f'      </a>'
    )


def render_fallback(destination):
    name = short_name(destination)
    uri = html.escape(destination["web_uri"])
    return f'      <a href="{uri}">{html.escape(name)}网页 ↗</a>'


def replace_region(text, name, lines):
    start = f"<!-- GENERATED:{name}:START -->"
    end = f"<!-- GENERATED:{name}:END -->"
    pattern = re.compile(re.escape(start) + r".*?" + re.escape(end), re.S)
    if pattern.search(text) is None:
        raise ValueError(f"index.html 缺少 {name} 生成标记")
    body = start + "\n" + "\n".join(lines) + "\n      " + end
    return pattern.sub(lambda _: body, text, count=1)


def update_manifest(destinations):
    manifest = json.loads(read_text(MANIFEST))
    manifest["shortcuts"] = [
        {
            "name": destinations[key]["title"],
            "short_name": short_name(destinations[key]),
            "url": destinations[key]["app_uris"][0],
        }
        for key in WEB_KEYS
    ]
    write_text(MANIFEST, json.dumps(manifest, ensure_ascii=False, indent=2) + "\n")


def main():
    destinations = parse_destinations(read_text(DESTINATION))
    missing = [key for key in WEB_KEYS if key not in destinations]
    if missing:
        raise SystemExit("Destination.java 缺少平台：" + ", ".join(missing))

    index = read_text(INDEX)
    index = replace_region(
        index, "ENTRIES", [render_entry(destinations[key]) for key in WEB_KEYS]
    )
    index = replace_region(
        index, "FALLBACKS", [render_fallback(destinations[key]) for key in WEB_KEYS]
    )
    write_text(INDEX, index)
    update_manifest(destinations)
    print("已同步 Web 入口：" + "、".join(destinations[key]["title"] for key in WEB_KEYS))


if __name__ == "__main__":
    main()
