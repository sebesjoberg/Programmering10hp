from uppgift4 import sequence as sc


def sequencechecker():
    number = 1
    max = 0

    while number <= 50:
        length = sc(number)
        print(number, ' has sequence length ', length)
        if length > max:
            max = length
            maxNumber = number

        number += 1
    print('maximum is at ', maxNumber, ' and is ', max)


if __name__ == "__main__":
    sequencechecker()
