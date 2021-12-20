from urllib.request import Request, urlopen

import requests
from bs4 import BeautifulSoup
import urllib3
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from urllib3.util import request
from webdriver_manager.chrome import ChromeDriverManager

url = "https://www.ratebeer.com/search?q=beer&tab=beer"
r = requests.get(url)
print(r.json())
options = Options()
options.add_argument('--headless')
options.add_argument('--disable-gpu')
browser = webdriver.Chrome(ChromeDriverManager().install())
browser.get(url)
html = browser.page_source
soup = BeautifulSoup(html,"html.parser",)
# container = soup.find_all('div', attrs = {
 # 'class': 'BeerTab___StyledDiv-gWeJQq JvNzg'
# })
#print(soup.prettify())


