#!/bin/bash
# 简单测试脚本 - 验证重构后的代码结构

echo "=== ProcessTask 重构验证 ==="
echo ""

# 检查 ProcessTask.java 是否存在
if [ -f "src/main/java/com/alibaba/cloud/ai/examples/shelluse/ProcessTask.java" ]; then
    echo "✅ ProcessTask.java 文件已创建"
    echo "   行数: $(wc -l < src/main/java/com/alibaba/cloud/ai/examples/shelluse/ProcessTask.java)"
else
    echo "❌ ProcessTask.java 文件不存在"
    exit 1
fi

# 检查 StreamingShellExample.java 是否已更新
if grep -q "ProcessTask task = new ProcessTask" "src/main/java/com/alibaba/cloud/ai/examples/shelluse/StreamingShellExample.java"; then
    echo "✅ StreamingShellExample.java 已更新使用 ProcessTask"
else
    echo "❌ StreamingShellExample.java 未正确使用 ProcessTask"
    exit 1
fi

# 检查内部类是否已移除
if grep -q "private class ProcessTask" "src/main/java/com/alibaba/cloud/ai/examples/shelluse/StreamingShellExample.java"; then
    echo "❌ StreamingShellExample.java 仍包含 ProcessTask 内部类"
    exit 1
else
    echo "✅ ProcessTask 内部类已成功移除"
fi

echo ""
echo "=== 代码结构验证 ==="
echo ""

# 显示关键类结构
echo "📦 ProcessTask.java 关键方法:"
grep -E "(public|private).*(class|void|int|Integer)" "src/main/java/com/alibaba/cloud/ai/examples/shelluse/ProcessTask.java" | head -10

echo ""
echo "📦 StreamingShellExample.java 调用 ProcessTask:"
grep -A 2 "ProcessTask task" "src/main/java/com/alibaba/cloud/ai/examples/shelluse/StreamingShellExample.java"

echo ""
echo "✅ 所有验证通过!"
echo ""
echo "重构总结:"
echo "  - ProcessTask 已提取为独立类"
echo "  - 构造函数负责初始化 ProcessBuilder"
echo "  - call() 方法负责启动进程和协调执行"
echo "  - 包含 3 个私有方法: readOutputWithTimeout, waitForProcessCompletion, cleanup"
echo "  - StreamingShellExample 简化为只创建和提交任务"
