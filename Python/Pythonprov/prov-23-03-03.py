# Credits go to Sebastian Sjöberg Test distributed by Joachim Parrow
# file to scramble a word, eg abcd into badc and bac into abc

import string

def scramble_word(text):
    textpiece = ""
    for index in range(0,len(text),2):
        try:
            if text[index] in string.ascii_letters and text[index+1] in string.ascii_letters:
                textpiece =textpiece + text[index+1]+text[index]
            else:
                textpiece = textpiece + text[index]+text[index+1]
        except IndexError:
            textpiece = textpiece + text[index]
    return textpiece


def scramble(text):
    newtext=[]
    for t in text.split():
        newtext.append(scramble_word(t)) 
    return ' '.join(newtext)



def test():
    cases = [['abcd','badc'],
             ['ab cd','ba dc'],
             ['abc','bac'],
             ['',''],
             [' ',''],
             ["hej jag heter",'ehj ajg ehetr'],
             ['hej!','ehj!'],
             ['!?a!ba','!?a!ab'],
             ["hej","ehj"]]
    for index in range(len(cases)):
        case = cases[index]
        try:
            assert scramble(case[0])==case[1]
            print('Passed test:',index)
        except:
            print('Did not pass test:',index)


def uppgift():
    test()

uppgift()