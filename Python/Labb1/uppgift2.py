import numpy as np


def sequence(number):
    print(number)
    while number != 1:
        if number % 2 == 0 and number != 0:
            number = number / 2

        else:
            number = 1 + number * 3
        print(number)


def test(nbWanted):  # test function
    numbers = np.random.randint(100, size=nbWanted)  # random cases
    for number in numbers:
        print('newone')
        sequence(number)

    numbers = [0, 1]  # edge/intresting cases
    for number in numbers:
        print('newone')
        sequence(number)


if __name__ == "__main__":
    test(5)
