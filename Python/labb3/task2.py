from task1 import stringsplitter, stringconverter, filereader
# file for returning dictionary where value is the words following key
# task 2 lab 3


def dictionary_creater(content):
    dictionary = {}
    for index in range(len(content)-1):
        if content[index] in dictionary:
            dictionary[content[index]] = dictionary[content[index]
                                                    ] + [content[index+1]]
        else:
            dictionary[content[index]] = [content[index+1]]
    return dictionary


def test():
    directory = "assets/txt1.txt"
    content = stringsplitter(stringconverter(filereader(directory)))
    print(dictionary_creater(content))


test()
