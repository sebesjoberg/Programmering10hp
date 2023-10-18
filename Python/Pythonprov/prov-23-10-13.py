# Credits go to Sebastian Sjöberg Test distributed by Joachim Parrow
# file to encrypt word list
# pythonprov 2023-10-13

def encrypt(word_list):
    crypto = ""
    for index in range(len(word_list[0])):
        for word in word_list:
            crypto += word[index]
        
    return crypto

def test():
    cases = [[["ILOV","EPRO","GRAM","MING"],"IEGMLPRIORANVOMG"]]
    
    
    for index in range(len(cases)):
        try:
            assert encrypt(cases[index][0])==cases[index][1]
            print('Passed test:',index, cases[index])
        except:
            print('Did not pass test:',index, cases[index])
if __name__ =="__main__":
    test()
