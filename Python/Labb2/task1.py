# Credits go to Sebastian Sjöberg
# # file for checking if array is sorted
# task 1 lab 2
def is_sorted(arr):

    for index in range(0, len(arr) - 1):
        if arr[index] > arr[index + 1]:
            return False
    return True


def test():
    arrays = [[], [1, 2, 3], [55],
              [55, 22, 1, 4, 5, 10]]
    for arr in arrays:
        if is_sorted(arr):
            print(arr, " is sorted")
        else:
            print(arr, " is not sorted")


if __name__ == "__main__":
    test()
