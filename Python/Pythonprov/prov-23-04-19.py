
def positive_gain(money_gains):
    m = max(money_gains)
    if m<=0:
        return -1
    money_gains.reverse()
    rev_index = money_gains.index(m)
    
    return len(money_gains)-rev_index-1
def best_buy(prices):
    money_gains = []
    for index in range(len(prices)-1):
        money_gains.append(max(prices[index+1:])-prices[index])
    
    return positive_gain(money_gains)

def test():
    cases = [[1,2,3,2,18,1,2],[5,2,5,4,18,14,13,12,11]]
    expected = [0,1]
    for index in range(len(cases)):
        try:
            assert best_buy(cases[index])==expected[index]
            print('Passed test:',index, cases[index])
        except:
            print('Did not pass test:',index, cases[index])
       

def uppgift():
    test()


if __name__ == "__main__":
    uppgift()

