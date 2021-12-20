# This is a sample Python script.

# Press Maiusc+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import time
import bs4
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support import expected_conditions as ec


def scrapy():
    url = "https://www.ratebeer.com/search?q=beer&tab=beer"

    options = Options()
    options.add_argument('--headless')
    options.add_argument('--disable-gpu')

    browser = webdriver.Chrome(ChromeDriverManager().install(), chrome_options=options)
    browser.get(url)
    time.sleep(5)
    page_source = browser.page_source
    soup = bs4.BeautifulSoup(page_source, 'html.parser')

    i = 0
    links_ratebeer = open('C:\\Users\\matte\\Desktop\\links.txt', 'w')
    while i < 500:
        find_all_links(browser, soup, links_ratebeer)
        print('%s %d' % ('Counter: ', i))
        i += 1
    links_ratebeer.close()


def find_all_links(browser, soup, links_ratebeer):
    for item in soup.find_all(class_="BeerTab___StyledDiv-gWeJQq JvNzg"):
        href = item.find_all(class_='MuiTypography-root Text___StyledTypographyTypeless-bukSfn pzIrn '
                                    'colorized__WrappedComponent-hrwcZr bRPQdN Anchor___StyledText2-jwDTwU jYqICB '
                                    'px-4 py-4 fj-s MuiTypography-body2')[0]['href']
        links_ratebeer.write("https://www.ratebeer.com" + href + '\n')
    else:
        button = WebDriverWait(browser, 5).until(ec.presence_of_element_located((By.XPATH, '/html/body/div[1]/div['
                                                                                           '2]/div['
                                                                                           '2]/div/div/div/div['
                                                                                           '2]/div[2]/div[21]/div['
                                                                                           '2]/div/div[3]/button[2]')))
        browser.execute_script("arguments[0].scrollIntoView();", button)
        browser.execute_script("arguments[0].click();", button)


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    scrapy()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/

