#!/bin/bash

BRANCH="master"
if [ -n "$1" ]; then
        BRANCH=$1
fi
echo "Updating branch $BRANCH"

cd /etc/nba/dwca || exit 1

git pull origin $BRANCH
if [ "$?" != 0 ]; then
        echo "Unknown branch name: $BRANCH. Please use an existing branch name"
        echo "DWCA config was not updated!"
        exit 1
fi
git checkout $BRANCH
if [ "$?" != 0 ]; then
        echo "Checkout of branch failed. Please retry"
        echo "DWCA config was not updated!"
        exit 1
fi

echo "DWCA config was updated with the latest settings from branch $BRANCH"
exit 0