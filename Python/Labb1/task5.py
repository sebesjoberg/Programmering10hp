from task4 import sequence as sc
# file to calculate max seq length for int<=50
# task 5 lab 1


def sequencechecker():
    number = 1
    max = 0

    for number in range(1, 51):
        length = sc(number)
        print(number, ' has sequence length ', length)
        if length > max:
            max = length
            maxNumber = number

    print('maximum is at ', maxNumber, ' and is ', max)


if __name__ == "__main__":
    sequencechecker()
