# This is a sample Python script.

# Press Maiusc+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import time
from urllib.request import Request, urlopen

import bs4
import requests
from bs4 import BeautifulSoup
import urllib3
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from urllib3.util import request
from webdriver_manager.chrome import ChromeDriverManager


def scrapy():
    url = "https://www.ratebeer.com/search?q=beer&tab=beer"
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--disable-gpu')
    browser = webdriver.Chrome(ChromeDriverManager().install(), chrome_options=options)
    browser.get(url)
    # html = browser.page_source
    # soup = BeautifulSoup(open(url), 'html.parser')
    time.sleep(5)  # if you want to wait 3 seconds
    page_source = browser.page_source
    soup = bs4.BeautifulSoup(page_source, 'html.parser')
    # print(soup.prettify())
    container = soup.find_all('div', attrs={
        'class': 'BeerTab___StyledDiv-gWeJQq JvNzg'
    })
    print(container)


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    scrapy()
