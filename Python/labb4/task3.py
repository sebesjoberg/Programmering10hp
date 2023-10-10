# Credits go to Sebastian Sjöberg
from urllib.request import urlopen
# file to find all links on a website
# task 3 lab 4


def find_first_link(text):
    start = text.find('<ahref=') + 8
    end = -1
    for index in range(start, len(text)):
        end = index
        if text[index] == '"':
            break

    link = text[start:end]
    text = text[end:]

    return link, text


def find_all_links(text):
    links = set()
    text = text.replace(" ", "")

    while text.find('<ahref="') >= 0:
        next_link, text = find_first_link(text)
        if next_link is not None:
            links.add(next_link)
    return links


def web_linksgetter(url):
    page = urlopen(url)
    html_bytes = page.read()
    html = html_bytes.decode("utf-8")
    html = html.lower()
    links = find_all_links(html)
    return links


def test():
    try:
        url = "https://www.stssektionen.com/"
        print(web_linksgetter(url))
        url = "http://www.it.uu.se/katalog/bylastname"
        print(web_linksgetter(url))
    except Exception as e:
        print(e)



if __name__ == "__main__":
    test()
