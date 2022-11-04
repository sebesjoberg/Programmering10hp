import time
from task3 import bubblesort
import numpy as np
# file for checking bubblesort time outputs time
# task 4 lab 2


def timingbubble():
    times = []
    for n in range(5):
        array = np.random.randint(1000, size=10 ** n)

        start_time = time.perf_counter()
        bubblesort(array)
        t = time.perf_counter() - start_time
        times.append(t)
        print(t)
    return times


def main():
    times = timingbubble()
    print(times)


if __name__ == "__main__":
    main()
