import os


# ord defineras som en rad bokstäver alltså ej tecken såsom .?!"#¤%1234569
def filereader(directory):
    content = ""
    with open(directory) as file:
        for line in file.readlines():
            content = content + line.strip() + " "

    return content


def stringconverter(string):
    string = string.lower()
    #string = "".join(filter(lambda x: x.isalpha() or x.isspace(), string))
    new_string = ""
    for char in string:
        if char.isspace() or char.isalpha():
            new_string = new_string + char


    return new_string


def stringsplitter(string):
    string = string.split()
    return string


def dictionarycreater(content):
    dictionary = {}
    for word in content:
        if word in dictionary:
            dictionary[word] = dictionary[word] + 1
        else:
            dictionary[word] = 1
    return dictionary

def maxcount(dictionary):
    max_key = max(dictionary, key=dictionary.get)
    return max_key

def main(directory):
    content = stringsplitter(stringconverter(filereader(directory)))
    print(len(content))
    dictionary = dictionarycreater(content)
    print(dictionary)
    max_key = maxcount(dictionary)
    print(max_key)



def test():
    directory = "C:/Users/ss691/OneDrive/Skrivbord/Programmeringslabbar/Python/labb3/assets/txt1.txt"

    main(directory)


if __name__ == "__main__":
    test()

