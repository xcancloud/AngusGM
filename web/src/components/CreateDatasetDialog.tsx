import { Check } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';
import { Label } from './ui/label';
import { Checkbox } from './ui/checkbox';
import { RadioGroup, RadioGroupItem } from './ui/radio-group';
import { Slider } from './ui/slider';
import { useState } from 'react';
import { toast } from 'sonner';
import { Badge } from './ui/badge';

interface CreateDatasetDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function CreateDatasetDialog({ open, onOpenChange }: CreateDatasetDialogProps) {
  const [currentStep, setCurrentStep] = useState(1);
  const [datasetName, setDatasetName] = useState('');
  const [description, setDescription] = useState('');
  const [dataType, setDataType] = useState<'text' | 'table'>('table');
  const [tags, setTags] = useState<string[]>([]);
  const [tagInput, setTagInput] = useState('');
  
  // 预处理选项
  const [removeDuplicates, setRemoveDuplicates] = useState(true);
  const [cleanHTML, setCleanHTML] = useState(true);
  const [autoSegment, setAutoSegment] = useState(false);
  const [normalizeFormat, setNormalizeFormat] = useState(true);
  
  // 数据分割
  const [trainingSplit, setTrainingSplit] = useState([80]);
  const [validationSplit, setValidationSplit] = useState([10]);
  const [testSplit, setTestSplit] = useState([10]);
  
  // 处理优先级
  const [priority, setPriority] = useState<'standard' | 'high'>('standard');

  const steps = [
    { number: 1, title: '基本信息' },
    { number: 2, title: '配置处理' },
  ];

  // 标签颜色映射
  const getTagColor = (tag: string): string => {
    const colors = [
      'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
      'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
      'bg-pink-100 text-pink-700 dark:bg-pink-900/30 dark:text-pink-400',
      'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      'bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-400',
      'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400',
    ];
    
    const index = tag.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0) % colors.length;
    return colors[index];
  };

  const handleAddTag = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      const newTag = tagInput.trim();
      
      if (!newTag) return;
      
      if (newTag.length > 10) {
        toast.error('标签长度不能超过10个字符');
        return;
      }
      
      if (tags.length >= 5) {
        toast.error('最多只能添加5个标签');
        return;
      }
      
      if (tags.includes(newTag)) {
        toast.error('标签已存在');
        return;
      }
      
      setTags([...tags, newTag]);
      setTagInput('');
    }
  };

  const handleRemoveTag = (tagToRemove: string) => {
    setTags(tags.filter(tag => tag !== tagToRemove));
  };

  const handleNext = () => {
    if (currentStep === 1) {
      if (!datasetName.trim()) {
        toast.error('请输入数据集名称');
        return;
      }
      if (!description.trim()) {
        toast.error('请输入描述');
        return;
      }
    }
    
    if (currentStep < 2) {
      setCurrentStep(currentStep + 1);
    } else {
      // 创建数据集
      toast.success('数据集创建成功！');
      onOpenChange(false);
      // 重置表单
      setCurrentStep(1);
      setDatasetName('');
      setDescription('');
      setTags([]);
      setTagInput('');
    }
  };

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const renderStep1 = () => (
    <div className="py-6">
      <div className="grid grid-cols-2 gap-6">
        <div>
          <Label className="text-sm mb-2 block dark:text-gray-300">数据集名称</Label>
          <Input
            value={datasetName}
            onChange={(e) => setDatasetName(e.target.value)}
            placeholder="输入数据集名称"
            className="dark:bg-gray-800 dark:border-gray-700 dark:text-white"
          />
        </div>

        <div>
          <Label className="text-sm mb-2 block dark:text-gray-300">
            标签 <span className="text-gray-400">({tags.length}/5)</span>
          </Label>
          <Input
            value={tagInput}
            onChange={(e) => setTagInput(e.target.value)}
            onKeyDown={handleAddTag}
            placeholder="输入标签后按回车，最多5个，每个不超过10字符"
            className="dark:bg-gray-800 dark:border-gray-700 dark:text-white"
            disabled={tags.length >= 5}
            maxLength={10}
          />
          {tags.length > 0 && (
            <div className="flex flex-wrap gap-2 mt-2">
              {tags.map((tag) => (
                <Badge
                  key={tag}
                  className={`border-0 ${getTagColor(tag)}`}
                >
                  {tag}
                  <button
                    onClick={() => handleRemoveTag(tag)}
                    className="ml-1 hover:opacity-70 transition-opacity"
                  >
                    ×
                  </button>
                </Badge>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="mt-5">
        <Label className="text-sm mb-2 block dark:text-gray-300">描述</Label>
        <Textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="输入描述"
          rows={3}
          className="dark:bg-gray-800 dark:border-gray-700 dark:text-white resize-none"
        />
      </div>

      <div className="mt-5">
        <Label className="text-sm mb-3 block dark:text-gray-300">数据类型</Label>
        <div className="grid grid-cols-2 gap-4">
          <button
            onClick={() => setDataType('text')}
            className={`p-3 border-2 rounded-lg text-left transition-all ${
              dataType === 'text'
                ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20'
                : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'
            }`}
          >
            <div className="flex items-start gap-3">
              <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-0.5 ${
                dataType === 'text'
                  ? 'border-blue-500'
                  : 'border-gray-300 dark:border-gray-600'
              }`}>
                {dataType === 'text' && (
                  <div className="w-2.5 h-2.5 rounded-full bg-blue-500" />
                )}
              </div>
              <div>
                <div className="dark:text-white mb-0.5">文本数据</div>
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  文档、文章、对话等
                </div>
              </div>
            </div>
          </button>

          <button
            onClick={() => setDataType('table')}
            className={`p-3 border-2 rounded-lg text-left transition-all ${
              dataType === 'table'
                ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20'
                : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'
            }`}
          >
            <div className="flex items-start gap-3">
              <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-0.5 ${
                dataType === 'table'
                  ? 'border-blue-500'
                  : 'border-gray-300 dark:border-gray-600'
              }`}>
                {dataType === 'table' && (
                  <div className="w-2.5 h-2.5 rounded-full bg-blue-500" />
                )}
              </div>
              <div>
                <div className="dark:text-white mb-0.5">表格数据</div>
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  CSV、Excel等
                </div>
              </div>
            </div>
          </button>
        </div>
      </div>
    </div>
  );

  const renderStep2 = () => (
    <div className="py-6">
      <div className="grid grid-cols-2 gap-8">
        {/* 左列 */}
        <div className="space-y-5">
          {/* 预处理选项 */}
          <div>
            <h3 className="text-sm mb-3 dark:text-gray-300">预处理选项</h3>
            <div className="space-y-2.5">
              <div className="flex items-center gap-2">
                <Checkbox
                  id="remove-duplicates"
                  checked={removeDuplicates}
                  onCheckedChange={(checked) => setRemoveDuplicates(checked as boolean)}
                />
                <label
                  htmlFor="remove-duplicates"
                  className="text-sm dark:text-gray-300 cursor-pointer"
                >
                  去除重复数据
                </label>
              </div>
              
              <div className="flex items-center gap-2">
                <Checkbox
                  id="clean-html"
                  checked={cleanHTML}
                  onCheckedChange={(checked) => setCleanHTML(checked as boolean)}
                />
                <label
                  htmlFor="clean-html"
                  className="text-sm dark:text-gray-300 cursor-pointer"
                >
                  清理HTML标签
                </label>
              </div>
              
              <div className="flex items-center gap-2">
                <Checkbox
                  id="auto-segment"
                  checked={autoSegment}
                  onCheckedChange={(checked) => setAutoSegment(checked as boolean)}
                />
                <label
                  htmlFor="auto-segment"
                  className="text-sm dark:text-gray-300 cursor-pointer"
                >
                  自动分段
                </label>
              </div>
              
              <div className="flex items-center gap-2">
                <Checkbox
                  id="normalize-format"
                  checked={normalizeFormat}
                  onCheckedChange={(checked) => setNormalizeFormat(checked as boolean)}
                />
                <label
                  htmlFor="normalize-format"
                  className="text-sm dark:text-gray-300 cursor-pointer"
                >
                  标准化文本格式
                </label>
              </div>
            </div>
          </div>

          {/* 处理优先级 */}
          <div>
            <h3 className="text-sm mb-3 dark:text-gray-300">处理优先级</h3>
            <RadioGroup value={priority} onValueChange={(value) => setPriority(value as 'standard' | 'high')}>
              <div className="flex items-center gap-6">
                <div className="flex items-center gap-2">
                  <RadioGroupItem value="standard" id="standard" />
                  <label htmlFor="standard" className="text-sm dark:text-gray-300 cursor-pointer">
                    标准
                  </label>
                </div>
                <div className="flex items-center gap-2">
                  <RadioGroupItem value="high" id="high" />
                  <label htmlFor="high" className="text-sm dark:text-gray-300 cursor-pointer">
                    高优先级
                  </label>
                </div>
              </div>
            </RadioGroup>
          </div>
        </div>

        {/* 右列 - 数据分割 */}
        <div>
          <h3 className="text-sm mb-3 dark:text-gray-300">数据分割</h3>
          <div className="space-y-5">
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm text-gray-600 dark:text-gray-400">训练集</span>
                <span className="text-sm dark:text-white">{trainingSplit[0]}%</span>
              </div>
              <Slider
                value={trainingSplit}
                onValueChange={setTrainingSplit}
                max={100}
                step={1}
                className="w-full"
              />
            </div>
            
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm text-gray-600 dark:text-gray-400">验证集</span>
                <span className="text-sm dark:text-white">{validationSplit[0]}%</span>
              </div>
              <Slider
                value={validationSplit}
                onValueChange={setValidationSplit}
                max={100}
                step={1}
                className="w-full"
              />
            </div>
            
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm text-gray-600 dark:text-gray-400">测试集</span>
                <span className="text-sm dark:text-white">{testSplit[0]}%</span>
              </div>
              <Slider
                value={testSplit}
                onValueChange={setTestSplit}
                max={100}
                step={1}
                className="w-full"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[85vh] overflow-hidden p-0 dark:bg-gray-900 dark:border-gray-700">
        {/* Header */}
        <DialogHeader className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <DialogTitle className="text-xl dark:text-white">创建数据集</DialogTitle>
          <DialogDescription className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            按照步骤创建和配置您的数据集
          </DialogDescription>
        </DialogHeader>

        {/* Step Indicator */}
        <div className="px-6 py-6 border-b border-gray-200 dark:border-gray-700">
          <div className="flex items-center justify-between max-w-md mx-auto">
            {steps.map((step, index) => (
              <div key={step.number} className="flex items-center flex-1">
                <div className="flex flex-col items-center flex-1">
                  <div
                    className={`w-9 h-9 rounded-full flex items-center justify-center transition-all ${
                      step.number === currentStep
                        ? 'bg-blue-500 text-white'
                        : step.number < currentStep
                        ? 'bg-green-500 text-white'
                        : 'bg-gray-200 dark:bg-gray-700 text-gray-500 dark:text-gray-400'
                    }`}
                  >
                    {step.number < currentStep ? (
                      <Check className="w-5 h-5" />
                    ) : (
                      <span>{step.number}</span>
                    )}
                  </div>
                  <span
                    className={`mt-2 text-sm ${
                      step.number === currentStep
                        ? 'text-blue-500'
                        : step.number < currentStep
                        ? 'text-green-500'
                        : 'text-gray-500 dark:text-gray-400'
                    }`}
                  >
                    {step.title}
                  </span>
                </div>
                {index < steps.length - 1 && (
                  <div
                    className={`h-0.5 flex-1 mx-2 mb-6 transition-all ${
                      step.number < currentStep
                        ? 'bg-green-500'
                        : 'bg-gray-200 dark:bg-gray-700'
                    }`}
                  />
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Content */}
        <div className="px-8 overflow-y-auto" style={{ maxHeight: 'calc(85vh - 230px)' }}>
          {currentStep === 1 && renderStep1()}
          {currentStep === 2 && renderStep2()}
        </div>

        {/* Footer */}
        <div className="px-8 py-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
          <div>
            {currentStep === 2 && (
              <Button
                variant="outline"
                onClick={() => {
                  toast.success('已保存为草稿');
                  onOpenChange(false);
                  setCurrentStep(1);
                }}
                className="dark:bg-gray-800 dark:border-gray-700 dark:text-white"
              >
                保存为草稿
              </Button>
            )}
          </div>
          <div className="flex items-center gap-3">
            {currentStep > 1 && (
              <Button
                variant="outline"
                onClick={handleBack}
                className="dark:bg-gray-800 dark:border-gray-700 dark:text-white"
              >
                上一步
              </Button>
            )}
            <Button
              onClick={handleNext}
              className={currentStep === 2 ? 'bg-green-600 hover:bg-green-700 text-white' : 'bg-blue-500 hover:bg-blue-600 text-white'}
            >
              {currentStep === 2 ? '确认并创建' : '下一步'}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
