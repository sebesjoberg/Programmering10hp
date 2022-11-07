# Credits go to Sebastian Sjöberg and Joachim Parrow
import string
EMAIL_CHARS = string.ascii_letters + string.digits + '.'
# some assets for lab 4, including finding name part of email, adress part of email and finding first emial in string


def find_name_start(text, at_index):
    first_index = 0
    for index in range(at_index-1, -1, -1):  # baklänges!
        if text[index] not in EMAIL_CHARS:
            first_index = index + 1
            break
    return first_index


def find_name_end(text, at_index):
    last_index = len(text)
    for index in range(at_index+1, len(text)):
        if text[index] not in EMAIL_CHARS:
            last_index = index
            break
    return last_index


def find_first_email(text):
    at_index = text.find('@')
    if at_index < 0:
        return None
    name_start = find_name_start(text, at_index)
    if name_start == at_index:
        return None
    name_end = find_name_end(text, at_index)

    if at_index == name_end-1:
        return None

    return text[name_start: name_end]
