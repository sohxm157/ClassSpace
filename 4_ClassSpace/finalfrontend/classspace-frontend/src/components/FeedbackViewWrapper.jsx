import React from "react";
import { useParams } from "react-router-dom";
import FeedbackView from "./FeedbackView";

export default function FeedbackViewWrapper() {
  const { lectureId } = useParams();
  console.log("param lectureId:", lectureId, "as number:", Number(lectureId));

  return <FeedbackView lectureId={Number(lectureId)} />;
}
