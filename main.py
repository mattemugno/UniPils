# This is a sample Python script.

# Press Maiusc+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.

import pandas as pd


def pd_read():
    train = pd.read_csv('C:\\Users\\matte\\Desktop\\reviews.csv')
    train.drop('text', inplace=True, axis=1)
    data = train.dropna()
    print(data.columns.tolist())
    data.to_csv('C:\\Users\\matte\\Desktop\\reviews_final.csv', index=False, index_label=None)
    # data = train.sample(frac=.25)
    # data.to_csv('C:\\Users\\matte\\Desktop\\reviews_sample.csv')


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    pd_read()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/

