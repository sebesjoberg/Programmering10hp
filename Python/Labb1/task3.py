# file for checking if digit sum^3=number for int<=10000
# task 3 lab 1
from task1 import siffsum as ssf


def checker(number):
    if number == ssf(number) ** 3:
        return True
    return False


def test():
    for number in range(10001):
        if checker(number):
            print(number, "satisfies condition")


if __name__ == "__main__":
    test()
