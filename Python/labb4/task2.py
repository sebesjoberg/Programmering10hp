# file to get all emails from a website
# task 2 lab 4
from urllib.request import urlopen
from task1 import find_all_emails


def email_getter(url):
    page = urlopen(url)
    html_bytes = page.read()
    html = html_bytes.decode("utf-8")

    email_set = find_all_emails(html)
    print(email_set)
    print(len(email_set), " emails were found")


def test():
    urls = ["http://www.it.uu.se/katalog/bylastname",
            "http://user.it.uu.se/~joachim/", "https://www2.uu.se/student"]
    for url in urls:
        email_getter(url)
