def is_sorted(arr):

    for i in range(0, len(arr) - 1):
        if arr[i] > arr[i + 1]:
            return False
    return True


def test():
    arrays = [[], [1, 2, 3], [55],
              [55, 22, 1, 4, 5, 10]]
    for arr in arrays:
        if is_sorted(arr):
            print(arr, " är sorterad")
        else:
            print(arr, " är inte sorterad")


if __name__ == "__main__":
    test()
