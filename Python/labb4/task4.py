from task3 import web_linksgetter
from urllib.parse import urljoin
from urllib.request import urlopen
# file to find all dead links on website
# task 4 lab 4


def find_all_full_links(url):
    links = web_linksgetter(url)
    full_links = set()
    for link in links:
        newlink = urljoin(url, link)
        full_links.add(newlink)
    return full_links


def dead_links_checker(url):
    links = find_all_full_links(url)
    dead_links = set()
    http_links = 0
    weird_links = 0
    for link in links:
        if "http" in link.lower():
            http_links += 1
            try:
                page = urlopen(url, timeout=3)
            except:
                dead_links.add(url)
        else:
            weird_links += 1

    print(dead_links)
    print(http_links, weird_links)


def test():
    url = "https://user.it.uu.se/~joachim/"
    dead_links_checker(url)
    url = "https://www.it.uu.se/katalog/bylastname"
    dead_links_checker(url)


if __name__ == "__main__":
    test()
