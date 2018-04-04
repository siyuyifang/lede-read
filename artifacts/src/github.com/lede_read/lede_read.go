package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"bytes"
)

type ReadChaincode struct {
}

// 报名用户
type EnrollUser struct {
	EnrollId   string `json:"enrollId"`
	Cellphone  string `json:"cellphone"`
	Username   string `json:"username"`
	Period     string `json:"period"`
	Status     int    `json:"status"`
	EnrollTime int16  `json:"enrollTime"`
}

// 文章实体
// type Article struct {
// 	ArticleId    string `json:"articleId"`
// 	Period       string `json:"period"`
// 	ArticleTitle string `json:"articleTitle"`
// 	ArticleUrl   string `json:"articleUrl"`
// 	Username     string `json:"username"`
// 	Cellphone    string `json:"cellphone"`
// 	Stars        int    `json:"stars"`
// 	Ctime        int16  `json:"ctime"`
// 	Utime        int16  `json:"utime"`
// }

// 评论实体
// type Comment struct {
// 	CommentId string `json:"commentId"`
// 	ArticleId string `json:"articleId"`
// 	Period    string `json:"period"`
// 	Cellphone string `json:"cellphone"`
// 	Star      int    `json:"star"`
// 	Ctime     int16  `json:"ctime"`
// }

// 初始化
func (t *ReadChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ReadChaincode Init")
	return shim.Success(nil)
}

// 方法调用入口
func (t *ReadChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ReadChaincode Invoke")
	function, args := stub.GetFunctionAndParameters()

	if function != "invoke" {
		return shim.Error("Unknown function call")
	}

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting at least 2")
	}

	// 报名
	if args[0] == "enrollByPeriod" {
		return t.enrollByPeriod(stub, args)
	}

	// 报名用户列表
	if args[0] == "enrollListByPeriod" {
		return t.enrollListByPeriod(stub, args)
	}

	// 提交读书笔记
	if args[0] == "insertArticle" {
		return t.insertArticle(stub, args)
	}

	// 修改读书笔记信息
	if args[0] == "updateArticle" {
		return t.updateArticle(stub, args)
	}

	// 查询我当期读书笔记
	if args[0] == "queryMyArticleByPeriod" {
		return t.queryMyArticleByPeriod(stub, args)
	}

	// 按期查询读书笔记列表
	if args[0] == "listArticleByPeriod" {
		return t.listArticleByPeriod(stub, args)
	}

	// 评论某篇笔记
	if args[0] == "commentByArticleId" {
		return t.commentByArticleId(stub, args)
	}

	// 获取某篇笔记的评论列表
	if args[0] == "listCommentByArticleId" {
		return t.listCommentByArticleId(stub, args)
	}

	// 获取用户在某期评论过的笔记数量
	if args[0] == "getCommentCountByUserId" {
		return t.getCommentCountByUserId(stub, args)
	}

	return shim.Error("Invalid invoke function name. " + function)
}

// 当前登录用户提交本期报名，注意检查本期是否已报名
func (t *ReadChaincode) enrollByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	jsonStr := args[1]
	var enrollUser EnrollUser
	err := json.Unmarshal([]byte(jsonStr), &enrollUser)
	if err != nil {
		shim.Error("Unmarshal failed " + jsonStr)
	}

	// 检查是否重复提交
	result, err := stub.GetState(enrollUser.EnrollId)
	if err != nil {
		shim.Error("getState error, enrollId =" + enrollUser.EnrollId)
	}
	if result != nil {
		return pb.Response{Status: 4002, Message: "already enroll. no need to do.", Payload: nil}
	}

	// 检查本期是否已报名
	sql := fmt.Sprintf("{\"selector\":{\"cellphone\":\"%s\",\"period\":\"%s\"}}", enrollUser.Cellphone, enrollUser.Period)
	sqi, err := stub.GetQueryResult(sql)
	if err != nil {
		return shim.Error("getState error, enrollId =" + enrollUser.EnrollId)
	}
	defer sqi.Close()
	if sqi.HasNext() {
		return pb.Response{Status: 4002, Message: "already enroll. no need to do.", Payload: nil}
	}

	// 保存报名信息
	err = stub.PutState(enrollUser.EnrollId, []byte(jsonStr))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte(jsonStr))
}

// 查询当前期数已报名用户列表
func (t *ReadChaincode) enrollListByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	period := args[1]
	sql := fmt.Sprintf("{\"selector\":{\"period\":\"%s\"}}", period)
	result, err := stub.GetQueryResult(sql)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer result.Close()
	var buffer bytes.Buffer
	buffer.WriteString("[")

	firstSplit := false
	for result.HasNext() {
		response, err := result.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		if firstSplit {
			buffer.WriteString(",")
		}
		buffer.WriteString(string(response.Value))
		firstSplit = true
	}
	buffer.WriteString("]")

	return shim.Success(buffer.Bytes())
}

// 插入文章记录，注意校验插入记录是否重复（同一时期内只允许一条文章记录）
func (t *ReadChaincode) insertArticle(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 更新文章记录
func (t *ReadChaincode) updateArticle(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 查询当前登录人对应期数的文章
func (t *ReadChaincode) queryMyArticleByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 获取对应期数的文章列表
func (t *ReadChaincode) listArticleByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 评论某篇文章笔记，注意是否评论判断
func (t *ReadChaincode) commentByArticleId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 获取某篇笔记的评论集
func (t *ReadChaincode) listCommentByArticleId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 获取指定期数，评论人一共评论的文章笔记数量
func (t *ReadChaincode) getCommentCountByUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

func main() {
	err := shim.Start(new(ReadChaincode))
	if err != nil {
		fmt.Printf("Error starting Read chaincode %s", err)
	}
}