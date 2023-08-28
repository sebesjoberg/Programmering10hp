# Credits go to Sebastian Sjöberg Test distributed by Joachim Parrow
# file to calibrate thermometer
# pythonprov 2022-10-14

def calibrate(ref,measured):
    total_diff = 0
    for index in range(len(ref)):
        diff = round(ref[index]-measured[index],2)
        if abs(diff) > 0.5:
            return -1000
        total_diff +=diff
    print(total_diff)
    return round(total_diff/len(ref),1)

def test():
    cases = [[[2,3,6,1,18],[2.1,3.2,6,1.05,17.9],-0.1],
             [[2,5,6,8,19],[2,5.1,6.2,8.5,19.6],-1000]]
    
        
    for index in range(len(cases)):
        try:
            assert cases[index][2]==calibrate(cases[index][0],cases[index][1])
            print('Passed test:',index, cases[index])
        except:
            print('Did not pass test:',index, cases[index])
if __name__ =="__main__":
    test()
