
from urllib.request import urlopen
from uppgift1 import find_all_emails


def email_getter(url):
    page = urlopen(url)
    html_bytes = page.read()
    html = html_bytes.decode("utf-8")

    email_set = find_all_emails(html)
    print(email_set)
    print(len(email_set)," emails were found")



url = "http://www.it.uu.se/katalog/bylastname"
email_getter(url)
url = "http://user.it.uu.se/~joachim/"
url = "https://www2.uu.se/student"

email_getter(url)