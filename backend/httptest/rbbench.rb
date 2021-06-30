#!/usr/bin/ruby

require 'socket'

server = TCPServer.new('127.0.0.1', 8080)

while connection = server.accept
  headers = []
  length  = 0

  while line = connection.gets
    headers << line

    if line =~ /^Content-Length:\s+(\d+)/i
      length = $1.to_i
    end

    break if line == "\r\n"
  end

  body = connection.readpartial(length)

  connection.print "Hello World"

  connection.close
end
