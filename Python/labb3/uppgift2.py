from uppgift1 import stringsplitter, stringconverter, filereader
def dictionary_creater(content):
    dictionary = {}
    for index in range(len(content)-1):
        if content[index] in dictionary:
            dictionary[content[index]] = dictionary[content[index]] + [content[index+1]]
        else:
            dictionary[content[index]] = [content[index+1]]
    return dictionary

def test():
    directory = "C:/Users/ss691/OneDrive/Skrivbord/Programmeringslabbar/Python/labb3/assets/txt1.txt"
    content = stringsplitter(stringconverter(filereader(directory)))
    print(dictionary_creater(content))
test()