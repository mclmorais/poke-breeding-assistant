"""Rewrite Kotlin-synthetic view references to ViewBinding properties.

usage: synthetic-to-binding.py FILE LAYOUT BINDING_EXPR [RECEIVER...]

Bare ids from LAYOUT become BINDING_EXPR.camelId. With RECEIVERs, only
`receiver.snake_id` forms are rewritten, to `BINDING_EXPR.camelId`.
"""
import re, sys
from pathlib import Path

src, layout, binding, *receivers = sys.argv[1:]
res = Path(__file__).resolve().parent.parent / 'app/src/main/res/layout' / f'{layout}.xml'
# ids without an underscore (`view`, `layout`) collide with ordinary Kotlin names
ids = sorted({i for i in re.findall(r'android:id="@\+id/(\w+)"', res.read_text()) if '_' in i}, key=len, reverse=True)
camel = lambda s: re.sub(r'_(\w)', lambda m: m.group(1).upper(), s)
text = Path(src).read_text()
text = re.sub(rf'^import kotlinx\.android\.synthetic\.main\.{layout}(\.view)?\.\*\n', '', text, flags=re.M)
for i in ids:
    if receivers:
        alt = '|'.join(map(re.escape, receivers))
        text = re.sub(rf'\b(?:{alt})\??\.{i}\b', f'{binding}.{camel(i)}', text)
    else:
        text = re.sub(rf'(?<![.\w]){i}\b', f'{binding}.{camel(i)}', text)
Path(src).write_text(text)
