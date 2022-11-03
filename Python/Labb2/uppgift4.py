import time
from uppgift3 import bubblesort
import numpy as np
#runt n=4 alltså 10 000 börjar det ta lång tid cirka 30-35 sekunder
#n=5 tar alldeles för lång tid enligt estimation 1h då t=H(n^2)
#sort använder timsort som är typ mergesort har komplexitet n*log(n) så bättre
#sorterar delar för att få mindre lookups

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




