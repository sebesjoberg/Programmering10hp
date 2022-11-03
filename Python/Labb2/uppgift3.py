from uppgift1 import is_sorted

def bytintill(lista):
    switch = False
    for i in range(0, len(lista) - 1):
        if lista[i] > lista[i + 1]:
            lista[i], lista[i + 1] = lista[i + 1], lista[i]
            switch = True
    return switch


def bubblesort(arr):
    switch = True
    while switch:
        switch = bytintill(arr)



def test():
    arrays = [[1, 2, 3, 4, 5],
              [6, 7, 2, 4, 1, 8],
              [], [5, 0, 0, 0], [0]]
    for arr in arrays:
        bubblesort(arr)
        print(is_sorted(arr))



if __name__ == "__main__":
    test()
