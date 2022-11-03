from assets import find_first_email
def find_all_emails(text):
    emails = set()
    text = text.split(" ")
    for potential in text:
        potential = find_first_email(potential)
        if potential is not None:
            emails.add(potential)


    return emails
if __name__ == "__main__":
    text="@hello hi hello babay@. @blabla.bla bla@blabla bla@gmail.com"
    print(find_all_emails(text))











