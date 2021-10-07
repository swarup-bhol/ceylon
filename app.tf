provider "aws" {
  access_key = "AKIAWZC3ZZZMVGRZBIWN"
  secret_key = "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"
  region = "eu-central-1"
}

data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

resource "aws_instance" "machine" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t3.micro"

  key_name = "service"
  tags = {
    Name = "Ceylon"
  }
}
