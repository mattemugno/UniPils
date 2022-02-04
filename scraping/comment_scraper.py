# This is a sample Python script.

# Press Maiusc+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import time
import bs4
from selenium import webdriver
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support import expected_conditions as ec
from random import randrange
from datetime import timedelta, datetime

global soup
global count


def random_date(start, end):
    """
    This function will return a random datetime between two datetime
    objects.
    """
    delta = end - start
    int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
    random_second = randrange(int_delta)
    return start + timedelta(seconds=random_second)


def scrapy():
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--disable-gpu')
    global count, app
    count = 0
    with open('C:\\Users\\pucci\\Desktop\\links.txt', 'r') as file:
        for line in file:
            browser = webdriver.Chrome(ChromeDriverManager().install(), chrome_options=options)
            browser.get(line)
            time.sleep(5)
            print(line)
            comments = open('C:\\Users\\pucci\\Desktop\\comments.txt', 'a', encoding='utf8')
            i = 0
            while i < 200:
                find_all_comments(browser, comments)
                if(count == app):
                    break
                i += 1
            comments.close()
    file.close()


def find_all_comments(browser, comments):
    global soup, count, app
    page_source = browser.page_source
    soup = bs4.BeautifulSoup(page_source, 'html.parser')
    i = 0
    for div in soup.find_all(class_="BeerReviewListItem___StyledSection2-gzaqoT JrTsK"):
        try:
            button = WebDriverWait(browser, 5).until(ec.element_to_be_clickable((By.XPATH, '/html/body/div[1]/div['
                                                                                           '2]/div[2]/div/div/div/div['
                                                                                           '2]/div[4]/div/div[3]/div['
                                                                                           '' + str(
                i + 1) + ']/div/section['
                         '2]/div['
                         '1]/div/button')))
            browser.execute_script("arguments[0].scrollIntoView();", button)
            browser.execute_script("arguments[0].click();", button)
        except TimeoutException:
            print('[ERROR]: TimeoutException raised')
        finally:
            page_source = browser.page_source
            soup = bs4.BeautifulSoup(page_source, 'html.parser')
            div = soup.find_all(class_="BeerReviewListItem___StyledSection2-gzaqoT JrTsK")
            comment = div[i].text
            d1 = datetime.strptime('1/1/2018 1:30 PM', '%m/%d/%Y %I:%M %p')
            d2 = datetime.strptime('12/31/2021 4:50 AM', '%m/%d/%Y %I:%M %p')

            date = random_date(d1, d2)
            comments.write(
                '[date]: ' + str(date) + ' [comment]: ' + comment.replace('Show less', '').replace('Bottiglia',
                                                                                                   '') + '\n')
            #print('[date]: ' + str(date) + ' [comment]: ' + comment.replace('Show less', ''))
            i += 1
            count += 1
    else:
        try:
            button = WebDriverWait(browser, 7).until(ec.presence_of_element_located((By.XPATH, '/html/body/div[1]/div['
                                                                                               '2]/div['
                                                                                               '2]/div/div/div/div['
                                                                                               '2]/div[4]/div/div[3]/div['
                                                                                               '16]/div[2]/div/div['
                                                                                               '3]/button[2]')))
            browser.execute_script("arguments[0].scrollIntoView();", button)
            browser.execute_script("arguments[0].click();", button)
            page_source = browser.page_source
            soup = bs4.BeautifulSoup(page_source, 'html.parser')
        except TimeoutException:
            print('[ERROR]: TimeoutException raised')
            app = count
            print(count)


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    scrapy()
    print('!!!!!!!!!',count,'!!!!!!!!!!!')
    print(count)
