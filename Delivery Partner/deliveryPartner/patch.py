with open('pom.xml', 'r') as f:
    orig = f.read()

import re
patched = re.sub(r'<plugin>.*?maven-compiler-plugin.*?</plugin>', '', orig, flags=re.DOTALL)

with open('pom.xml', 'w') as f:
    f.write(patched)
