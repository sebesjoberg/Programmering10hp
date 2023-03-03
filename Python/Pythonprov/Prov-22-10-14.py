# Credits go to Sebastian Sjöberg Test distributed by Joachim Parrow
import string
# file to makerunes
# pythonprov 2022-10-14


def uppgift():
    test()


def test():
    cases = [["hejjj jag","HEJAG"], 
             ["",""], 
             ["HEJ Ja,menG Ud vaddårå? ajabajjjjja annars så","HEJAMENGUDVADRAJABAJANARS"],
             ["JJJJJJJJjjjJJJJJJJjjjjjjjjjjjjj","J"]]
    
    for index in range(len(cases)):
        case = cases[index]
        try:
            assert make_runes(case[0])==case[1]
            print('Passed test:',index, case[0])
        except:
            print('Did not pass test:',index, case[0])


def make_runes(text):
    text = nonasciiremover(text)
    text = makebig(text)
    text = removerepeating(text)
    return text


def removerepeating(text):
    lastchar = ""
    newtext = ""
    for char in text:
        if char != lastchar:
            newtext = newtext + char
        lastchar = char
    return newtext


def makebig(text):
    text = text.upper()
    return text


def nonasciiremover(text):
    newtext = ""
    lettersallowed = string.ascii_letters
    for char in text:
        if char in lettersallowed:
            newtext = newtext + char
    return newtext


uppgift()
