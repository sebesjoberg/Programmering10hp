from uppgift1 import siffsum as ssf


def checker(number):
    if number == ssf(number) ** 3:
        return True
    return False


def test():
    number = 0
    while number <= 10000:

        if checker(number):
            print(number, "satisfies condition")
        number += 1


test()

