# Credits go to Sebastian Sjöberg
import random
from task1 import stringsplitter, stringconverter, filereader
# file for generating new text from a text
# task 3 lab 3


def dictionary_creater(content):
    dictionary = {}
    for index in range(len(content)-1):
        if content[index] in dictionary:
            dictionary[content[index]] = dictionary[content[index]
                                                    ] + [content[index+1]]
        else:
            dictionary[content[index]] = [content[index+1]]
    return dictionary


def text_creater(dictionary, length):
    string = ""
    words = list(dictionary.keys())
    for i in range(length):
        word = random.choice(words)
        string += word + " "
        words = dictionary.get(word)

    return string


def test():
    directory = "assets/HP.txt"
    length = 100
    content = stringsplitter(stringconverter(filereader(directory)))
    content = dictionary_creater(content)
    print(text_creater(content, length))


test()
