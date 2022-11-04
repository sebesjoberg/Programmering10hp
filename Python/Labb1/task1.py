import numpy as np
# file to calculate digit sum
# task 1 lab 1


def siffsum(number):  # function to calculate digit sum
    sum = 0
    while number != 0:
        sum = sum + (number % 10)
        number = number // 10
    return sum


def test(nbwanted):  # test function
    numbers = np.random.randint(
        1000000, size=nbwanted).tolist()  # random cases
    numbers.extend([0, 55, 99, 0000, ""])  # edge/intresting cases

    for number in numbers:
        try:
            print('digit sum of: ', number, ' is: ', siffsum(number))
        except TypeError:
            print("the input was not a number")


if __name__ == "__main__":
    test(5)
