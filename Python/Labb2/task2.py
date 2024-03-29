# Credits go to Sebastian Sjöberg
# # file for getting initials of a name
# task 2 lab 2
def initialsgetter(name):
    name = name.split()
    initials = []
    for part in name:
        initials.append(part[0])
        initials.append(".")
    initials = "".join(initials)

    return initials


def test():
    names = ["Joachim Gunnar Parrow",
             "Sebastian Sjöberg",
             "Emil",
             ""]
    expected = ["J.G.P.", "S.S.", "E.", ""]
    for i in range(len(names)):
        print(names[i]," is expected to be: ",expected[i])
        print(expected[i] == initialsgetter(names[i]))


if __name__ == "__main__":
    test()
