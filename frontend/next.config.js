/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
  images: {
    domains: ['localhost'],
  },
  logging: {
    fetches: {
      fullUrl: true,
    },
    // Esta é a opção que você precisa:
    browserToTerminal: true, 
  }
}

module.exports = nextConfig
