import numpy as np
#conversion gör man själv
#duck typing ser det ur som en anka och låter som en anka är det en anka
def siffsum(number):  # function to calculate digit sum
    sum = 0
    while number != 0:
        sum = sum + (number % 10)
        number = number // 10
    return sum


def test(nbwanted):  # test function
    numbers = np.random.randint(1000000, size=nbwanted)  # random cases
    for number in numbers:
        print('digit sum of: ', number, ' is: ', siffsum(number))
    numbers = [0, 55, 99]  # edge/intresting cases
    for number in numbers:
        print('digit sum of: ', number, ' is: ', siffsum(number))


if __name__ == "__main__":
    test(5)
