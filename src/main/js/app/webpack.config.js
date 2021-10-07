var path = require('path');

module.exports = {
    entry: './src/index.js',
    devtool: 'sourcemaps',
    cache: true,
    mode: 'production',
    output: {
        path: path.resolve(__dirname,'../../resources/static'),
        filename: 'built/bundle.js'
    },
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            },
            {
                test: /\.(css|less|sass)$/,
                exclude: /(node_modules)/,
                use: [
                    'css-loader'
                ]
            }
        ]
    }
};
