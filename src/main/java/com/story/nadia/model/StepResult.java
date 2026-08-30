package com.story.nadia.model;

public sealed interface StepResult permits GameOver, NextNode, Victory {
}
