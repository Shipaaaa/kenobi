FROM python:3.5

MAINTAINER Vladislav Shipugin <vladshipugin@gmail.com>

WORKDIR /kenobi/load-prediction
COPY . /kenobi/load-prediction

RUN pip install --upgrade pip
RUN pip install --trusted-host pypi.python.org -r requirements.txt

EXPOSE 5000

CMD ["python", "prediction.py"]
