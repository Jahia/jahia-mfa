const path = require('path');
const webpack = require('webpack');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');

module.exports = (env, argv) => {
    const config = {
        entry: {
            main: path.resolve(__dirname, 'src/index.js')
        },
        output: {
            path: path.resolve(__dirname, '../src/main/resources/javascript/apps/'),
            filename: 'jahia.bundle.js',
            chunkFilename: '[name].jahia.[chunkhash:6].js',
           // jsonpFunction: 'patJsonp'
        },
        resolve: {
            mainFields: ['module', 'main'],
            extensions: ['.mjs', '.js', '.jsx', 'json']
        },
        optimization: {
            splitChunks: {
                maxSize: 4000000
            }
        },
        module: {
            rules: [
                {
                    test: /\.(js|jsx)$/,
                    include: [path.join(__dirname, 'src')],
                    exclude: /node_modules/,
                    use: {
                        loader: 'babel-loader',
                        options: {
                            plugins:[
                                ['transform-imports', {
                                    '@material-ui/icons': {
                                        transform: '@material-ui/icons/${member}',
                                        preventFullImport: true
                                    }
                                }]
                            ],
                        }
                    }
                },
                {
                    test: /\.css$/,
                     use: ["style-loader", "css-loader"],
                },
                {
                    // ASSET LOADER
                    // Reference: https://github.com/webpack/file-loader
                    // Copy png, jpg, jpeg, gif, svg, woff, woff2, ttf, eot files to output
                    // Rename the file using the asset hash
                    // Pass along the updated reference to your code
                    // You can add here any file extension you want to get copied to your output
                    test: /\.(png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot|otf)$/,
                    use: [
                        'file-loader'

                    ]
                }
            ]
        },

        mode: 'development'
    };

    config.devtool = (argv.mode === 'production') ? 'source-map' : 'eval-source-map';

    if (argv.analyze) {
        config.devtool = 'source-map';
        config.plugins.push(new BundleAnalyzerPlugin());
    }

    return config;
};
