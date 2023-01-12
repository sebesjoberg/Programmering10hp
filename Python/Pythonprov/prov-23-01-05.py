# Credits go to Sebastian Sjöberg Test distributed by Joachim Parrow
# file to calculate the max increase in sun_hours from one day to the nextnext day from a list of sun_hours per day

allowed = list(range(25))

def increase_max(sun_hours):
    maximum = -1
    for index in range(len(sun_hours)-2):
        if sun_hours[index] in allowed and sun_hours[index+2] in allowed:
            if sun_hours[index] <= sun_hours[index+1] and sun_hours[index+2] >= sun_hours[index+1]:
                maximum = max([maximum, sun_hours[index+2]-sun_hours[index]])
    if maximum == 0:
        maximum = -1
    return maximum




def test():
    cases = [[1,2,3,4,8],[],[2],[2,2],[3,3,3],[1,2,8,-16]]
    expected = [5,-1,-1,-1,-1,7]
    for index in range(len(cases)):
        case = cases[index]
        res = increase_max(case)
        print(case,"blir", res, res == expected[index])
        
def uppgift():
    test()

if __name__ == '__main__':
    uppgift()