import os
import re

def to_camel_case(snake_str):
    components = re.split('_|-', snake_str)
    return components[0] + ''.join(x.title() for x in components[1:])

def replace_in_file(file_path):
    with open(file_path, 'r') as file:
        content = file.read()

    content = re.sub(
        r'implementation\(project\(":(.*?)"\)\)',
        lambda match: 'implementation(projects.' + '.'.join(to_camel_case(part) for part in match.group(1).split(':')) + ')',
        content
    )

    contentApi = re.sub(
        r'api\(project\(":(.*?)"\)\)',
        lambda match: 'api(projects.' + '.'.join(to_camel_case(part) for part in match.group(1).split(':')) + ')',
        content
    )

    with open(file_path, 'w') as file:
        file.write(content)

    with open(file_path, 'w') as file:
        file.write(contentApi)

def process_directory(directory):
    for root, dirs, files in os.walk(directory):
        for name in files:
            if name.endswith('.kts'):
                file_path = os.path.join(root, name)
                replace_in_file(file_path)


directory_path = '.'
process_directory(directory_path)
