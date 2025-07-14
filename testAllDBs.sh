#!/bin/bash
#
# Copyright (C) 2003-2012 David E. Berry
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#
# A copy of the GNU Lesser General Public License may also be found at
# http://www.gnu.org/licenses/lgpl.txt
#

function test_db() {
  mvn -pl cpo-jdbc -am clean test -Dcpo.db=$1

  if [ $? -ne 0 ]; then
    echo "$1 did not pass the unit tests\n"
    exit 1
  fi

  echo "$1 passed the unit tests\n"
}

# Do a clean build
mvn clean compile

if [ $? -ne 0 ]; then
  echo 'Errors compiling the project'
  exit 1
fi

mvn test

if [ $? -ne 0 ]; then
  echo 'H2 did not pass the unit tests'
  exit 1
fi

echo 'H2 passed the unit tests'

test_db 'mysql'

test_db 'mariadb'

test_db 'postgres'

test_db 'oracle-xe'

echo 'All DBs passed the unit tests'
