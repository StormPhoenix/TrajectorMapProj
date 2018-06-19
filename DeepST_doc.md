以下程序经过测试，是运行成功的。但运行成功的环境最好以本人系统环境为准。

1. 系统环境
===
操作系统：Ubuntu 16.04
python库：

>tensorflow 1.0.0 版本
>keras 2.0.5 版本

2. 前期准备
=
2.1. 下载源代码，并安装。在命令行中输入如下命令，可以下载源代码

> git clone https://github.com/lucktroy/DeepST

然后源代码会下载到当前的目录中，文件名叫DeepST，在命令行中进入这个文件，输入以下命令

>python setup.py install

这个命令用于把 DeepST　程序安装到电脑中

2.2. 设置环境变量
程序运行前需要设置DATAPATH环境变量，因为程序中用到了这个变量，按照如下方式设置：

>export DATAPATH=[path_to_your_data]

这里的[path_to_your_data]用于指代DeepST里面的数据集所在的位置，这个数据集位于DeepST中的data位置，所以在本人系统里面的具体命令如下：

> export DATAPATH=/home/Developer/MyGraduateDesign/DeepST

另外，本程序如果需要运行，要在用户主目录下面修改 .keras/keras.json 文件的内容，若这个文件没有，则创建它，具体的内容如下：

>{
>	"epsilon" : 1e-07,
>       "floatx" : "float32",
>       "image_data_format" : "channels_first",
>       "backend" : "tensorflow"
>}

3. 运行步骤
=

程序里面有两个数据集合一个是出租车轨迹数据集，一个是自行车轨迹数据集，这两个数据集的路径是DeepST/scripts/papers/AAAI17，这个路径下面存在着上述两种数据集合以及相关的代码。我们需要做到就是选择一个数据集合并运行相应的的代码。在这里我们要进入 TaxiBJ 文件夹，并运行其中的代码，如下：

THEANO_FLAGS="device=gpu, float32" python exptTaxiBJ.py 2

然后在这里就等程序运行完毕。



