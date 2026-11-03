FROM python:3.12-slim

RUN pip install --no-cache-dir ansible kubernetes

WORKDIR /ansible

CMD ["ansible", "--version"]
