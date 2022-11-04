import numpy as np
# file to do the sequence
# task 2 lab 1


def sequence(number):
    seq = [number]
    while number != 1:
        if number % 2 == 0 and number != 0:
            number = number / 2

        else:
            number = 1 + number * 3
        seq.append(number)
    return seq


def test(nbWanted):  # test function
    numbers = np.random.randint(100, size=nbWanted).tolist()  # random cases

    numbers.extend([0, 1])  # edge/intresting cases

    for number in numbers:
        print('newone')
        seq = sequence(number)
        print(seq)


if __name__ == "__main__":
    test(5)
