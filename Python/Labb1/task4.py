# Credits go to Sebastian Sjöberg
import numpy as np
# file to calculate sequence length
# task 4 lab 1


def sequence(number):
    counter = 1
    while number != 1:
        counter += 1
        if number % 2 == 0 and number != 0:
            number = number / 2

        else:
            number = 1 + number * 3
    return counter


def test(nbWanted):  # test function
    numbers = np.random.randint(100, size=nbWanted).tolist()  # random cases
    numbers.extend([0, 1])   # edge/intresting cases
    for number in numbers:
        counter = sequence(number)
        print(number, ' has sequence length ', counter)


if __name__ == "__main__":
    test(5)
