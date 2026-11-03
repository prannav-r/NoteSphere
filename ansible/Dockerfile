FROM python:3.12-slim

RUN pip install --no-cache-dir ansible kubernetes \
    && ansible-galaxy collection install kubernetes.core

WORKDIR /ansible

CMD ["ansible", "--version"]
